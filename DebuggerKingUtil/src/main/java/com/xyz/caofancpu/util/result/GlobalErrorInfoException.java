package com.xyz.caofancpu.util.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class GlobalErrorInfoException extends Exception implements ErrorInfoInterface {

    private String code;

    private String msg;

    public GlobalErrorInfoException(ErrorInfoInterface infoInterface) {
        this.code = infoInterface.getCode();
        this.msg = infoInterface.getMsg();
    }

    public GlobalErrorInfoException(String msg) {
        this.code = GlobalErrorInfoEnum.GLOBAL_MSG.getCode();
        this.msg = msg;
    }

    public GlobalErrorInfoException() {
        this.code = GlobalErrorInfoEnum.GLOBAL_MSG.getCode();
        this.msg = GlobalErrorInfoEnum.GLOBAL_MSG.getMsg();
    }

    @Override
    public String getMessage() {
        return this.getMsg();
    }
}
