package cn.xpbootcamp.legacy_code.vo.exception;

/**
 * InvalidTransactionInfoException:
 * @author zhangxuhai
 * @date 2020/3/12
*/
public class InvalidTransactionInfoException extends Exception {
    public InvalidTransactionInfoException() {
        super("This is an invalid transaction info");
    }
}
