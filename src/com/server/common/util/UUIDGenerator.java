package com.server.common.util;

import java.util.UUID;

/**
 * Created by tian on 16/9/27.
 */

public class UUIDGenerator {
        private UUIDGenerator(){}

        public static String getCode(String prefix) {
            String newCode = prefix
                    + UUID.randomUUID().toString();
            return newCode;
        }
}
