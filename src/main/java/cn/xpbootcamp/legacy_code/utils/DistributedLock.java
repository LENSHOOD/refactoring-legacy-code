package cn.xpbootcamp.legacy_code.utils;

/**
 * DistributedLock:
 * @author zhangxuhai
 * @date 2020/3/10
*/
public interface DistributedLock {
    boolean lock(String transactionId);

    void unlock(String transactionId);
}
