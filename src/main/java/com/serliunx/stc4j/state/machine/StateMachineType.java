package com.serliunx.stc4j.state.machine;

/**
 * 状态机类型
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.4
 * @since 2026/3/16
 */
public enum StateMachineType {
    /**
     * 标准, 切换使用读写锁
     */
    STANDARD,

    /**
     * 并发型, 切换使用CAS乐观锁
     */
    CONCURRENT;
}
