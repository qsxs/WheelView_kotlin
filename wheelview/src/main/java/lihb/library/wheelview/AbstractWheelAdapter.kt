/*
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

import android.database.DataSetObserver
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * Abstract Wheel adapter.
 */
abstract class AbstractWheelAdapter : WheelView.WheelViewAdapter() {
    // Observers
    private var datasetObservers: MutableList<DataSetObserver>? = null

    override fun getEmptyItem(emptyView: View?, parent: ViewGroup?): View? {
        return null
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {
        if (datasetObservers == null) {
            datasetObservers = LinkedList()
        }
        datasetObservers!!.add(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        if (datasetObservers != null) {
            datasetObservers!!.remove(observer)
        }
    }

    /**
     * Notifies observers about data changing
     */
    protected fun notifyDataChangedEvent() {
        if (datasetObservers != null) {
            for (observer in datasetObservers!!) {
                observer.onChanged()
            }
        }
    }

    /**
     * Notifies observers about invalidating data
     */
    protected fun notifyDataInvalidatedEvent() {
        if (datasetObservers != null) {
            for (observer in datasetObservers!!) {
                observer.onInvalidated()
            }
        }
    }
}
