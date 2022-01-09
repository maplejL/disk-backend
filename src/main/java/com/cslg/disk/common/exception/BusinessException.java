package com.cslg.disk.common.exception;

import com.cslg.disk.common.IExceptionCode;

public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    protected IExceptionCode exCode;
    protected String[] params;

    public BusinessException(IExceptionCode code) {
        super(code.getError());
        this.exCode = code;
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(IExceptionCode code, String[] params) {
        super(code.getError());
        this.exCode = code;
        this.params = params;
    }

    public IExceptionCode getExCode() {
        return this.exCode;
    }

    protected String parseMessage(String message) {
        if (this.params == null) {
            return message;
        } else {
            String errorString = this.exCode.getError();

            for(int i = 0; i < this.params.length; ++i) {
                errorString = errorString.replace("{" + i + "}", this.params[i]);
            }

            return errorString;
        }
    }

    public String getMessage() {
        return this.exCode != null && !"".equals(this.exCode.getCode()) ? this.exCode.getCode() + ":" + this.parseMessage(this.exCode.getError()) : super.getMessage();
    }

}
