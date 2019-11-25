package com.xyz.caofancpu.mvc.config;

import com.fasterxml.classmate.ResolvedType;
import com.xyz.caofancpu.util.commonOperateUtils.enumType.IEnum;
import com.xyz.caofancpu.util.streamOperateUtils.CollectionUtil;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.Annotations;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.schema.ApiModelProperties;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * Swagger2枚举类对象属性解析插件
 *
 * @author ht-caofan
 */
@Component
public class Swagger2ModelPropertyPlugin implements ModelPropertyBuilderPlugin {

    @Override
    public void apply(ModelPropertyContext context) {
        Optional<ApiModelProperty> annotation = Optional.empty();

        if (context.getAnnotatedElement().isPresent()) {
            annotation = Optional.of(annotation.orElseGet(ApiModelProperties.findApiModePropertyAnnotation(context.getAnnotatedElement().get())::get));
        }
        if (context.getBeanPropertyDefinition().isPresent()) {
            annotation = Optional.of(annotation.orElseGet(Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), ApiModelProperty.class)::get));
        }
        final Class<?> rawPrimaryType = context.getBeanPropertyDefinition().get().getRawPrimaryType();
        // 过滤得到目标类型
        if (annotation.isPresent() && List.class.isAssignableFrom(rawPrimaryType)) {
            IEnum[] values = (IEnum[]) rawPrimaryType.getEnumConstants();
            final List<String> displayValues = CollectionUtil.transToList(Collections.singletonList(values), Object::toString);
            final AllowableListValues allowableListValues = new AllowableListValues(displayValues, rawPrimaryType.getTypeName());
//             固定设置为int类型
            final ResolvedType resolvedType = context.getResolver().resolve(int.class);
            context.getBuilder()
//                    .description(CollectionUtil.join(displayValues, ","))
                    .allowableValues(allowableListValues)
                    .qualifiedType("Integer");
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
