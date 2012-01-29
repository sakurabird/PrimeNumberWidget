/*
 * Copyright 2012 sakura_fish<neko3genki@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sakurafish.android.primenumberwidget;

/**
 * @author sakura
 */
public interface MyException {
    /**
     * 不正な数値が入力された時に投げる例外
     */
    @SuppressWarnings("serial")
    public static class IllegalNumberException extends Exception {

        /**
         * @param string
         */
        public IllegalNumberException(String string) {
            super(string);
        }
    }
}
