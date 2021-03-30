/*
 * IApplyTransaction.kt - pair-modeling-prototype
 * Copyright © 2021 HyodaKazuaki.
 *
 * Released under the MIT License.
 * see https://opensource.org/licenses/MIT
 */

package jp.ex_t.kazuaki.change_vision.diagram

import jp.ex_t.kazuaki.change_vision.Transaction

interface IApplyTransaction {
    fun apply(transaction: Transaction)
}