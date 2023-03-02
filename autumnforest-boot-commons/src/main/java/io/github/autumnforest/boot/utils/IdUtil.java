package io.github.autumnforest.boot.utils;

import org.apache.commons.lang3.StringUtils;

public class IdUtil {

    /**
     * 生成id， 当前毫秒数 + 1位ip尾数 + 2位循环自增数值:11-99
     * @return id
     */
    public static Long sequenceId() {
        return SequenceId.getMillisId();
    }

    private static class SequenceId {
        private static int increment = 10;
        private static final String IP_FLAG = ipTail();
        /**
         * 自增范围， 给id添加两位自增数值， 减少高并发冲突
         */
        private static final int INCREASE_NUM = 99;


        /**
         * 取当前毫秒时间 + 额外数字（避免并发重复）
         *
         * @return System.currentTimeMillis() + ip最后一位 + 10-99之间的一个整数
         */
        public static Long getMillisId() {
            synchronized (IdUtil.class) {
                increment++;
                if (increment % INCREASE_NUM == 0) {
                    increment = 10;
                }

            }
            return Long.valueOf(System.currentTimeMillis() + IP_FLAG + increment);
        }

        private static String ipTail() {
            String last = StringUtils.substringAfterLast(MachineUtil.getRealInetAddress().getHostAddress(), ".");
            return StringUtils.substring(last, last.length() - 1);
        }
    }
}
