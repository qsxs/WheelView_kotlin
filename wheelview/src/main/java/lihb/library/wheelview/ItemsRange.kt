/*
 *  Android Wheel Control.
 *  https://code.google.com/p/android-wheel/
 *  
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package lihb.library.wheelview

/**
 * Range for visible items.
 */
class ItemsRange
/**
 * Constructor
 * @param first the number of first item
 * @param count the count of items
 */
@JvmOverloads constructor(// First item number
        /**
         * Gets number of  first item
         * @return the number of the first item
         */
        val first: Int = 0, // Items count
        /**
         * Get items count
         * @return the count of items
         */
        val count: Int = 0) {

    /**
     * Gets number of last item
     * @return the number of last item
     */
    val last: Int
        get() = first + count - 1

    /**
     * Tests whether item is contained by range
     * @param index the item number
     * @return true if item is contained
     */
    operator fun contains(index: Int): Boolean {
        return index in first..last
    }
}
/**
 * Default constructor. Creates an empty range
 */