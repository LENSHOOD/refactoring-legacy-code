package cn.xpbootcamp.legacy_code.utils.lock;

/**
 * DoubleLockManager:
 * @author zhangxuhai
 * @date 2020/3/12
*/
public class DoubleLockManager {
    private DistributedLock distributedLock;

    public DoubleLockManager(DistributedLock distributedLock) {
        this.distributedLock = distributedLock;
    }

    public DoubleLock obtainLock(long id1, long id2) {
        return new DoubleLock(String.valueOf(id1), String.valueOf(id2));
    }

    public class DoubleLock {
        String key1;
        String key2;

        private DoubleLock(String key1, String key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        public boolean tryLock() {
            if (!distributedLock.lock(key1)) {
                return false;
            }

            boolean acquired = false;
            try {
                acquired = distributedLock.lock(key2);
                return acquired;
            } finally {
                if (!acquired) {
                    distributedLock.unlock(key1);
                }
            }
        }

        public void unlock() {
            try {
                distributedLock.unlock(key2);
            } finally {
                distributedLock.unlock(key1);
            }
        }

    }
}
