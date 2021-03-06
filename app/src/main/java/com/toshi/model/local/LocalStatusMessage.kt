/*
 * 	Copyright (c) 2017. Toshi Inc
 *
 * 	This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.toshi.model.local

import android.support.annotation.IntDef
import com.toshi.R
import com.toshi.view.BaseApplication

class LocalStatusMessage(
        val type: Long,
        val sender: User?,
        val newUsers: List<User>?
) {

    constructor(type: Long) : this(type, null, null)
    constructor(type: Long, sender: User?) : this(type, sender, null)

    companion object {
        const val NEW_GROUP = 0L
        const val USER_LEFT = 1L
        const val USER_ADDED = 2L
    }

    fun loadString(isSenderLocalUser: Boolean): String {
        return when (type) {
            NEW_GROUP -> BaseApplication.get().getString(R.string.lsm_group_created)
            USER_LEFT -> formatUserLeftMessage()
            USER_ADDED -> formatUserAddedMessage(isSenderLocalUser)
            else -> ""
        }
    }

    private fun formatUserLeftMessage() = String.format(BaseApplication.get().getString(R.string.lsm_user_left), sender?.displayName)

    private fun formatUserAddedMessage(isSenderLocalUser: Boolean): String {
        val displayNameOfSender = if (isSenderLocalUser) BaseApplication.get().getString(R.string.you)
        else sender?.displayName ?: ""

        return newUsers?.let {
            return when (newUsers.size) {
                0 -> ""
                1 -> BaseApplication.get().getString(R.string.lsm_added_user, displayNameOfSender, newUsers[0].displayName)
                in 2..3 -> {
                    val firstUsers = joinToString(newUsers.size - 1, newUsers)
                    val lastUser = newUsers.last().displayName
                    BaseApplication.get().getString(R.string.lsm_added_users, displayNameOfSender, firstUsers, lastUser)
                }
                else -> {
                    val numberOfNamesToShow = 2
                    val firstUsers = joinToString(numberOfNamesToShow, newUsers)
                    val numberOfLeftoverUsers = newUsers.size - numberOfNamesToShow
                    BaseApplication.get().getString(R.string.lsm_added_users_wrapped, displayNameOfSender, firstUsers, numberOfLeftoverUsers)
                }
            }
        } ?: ""
    }

    private fun joinToString(n: Int, newUsers: List<User>): String {
        return newUsers
                .take(n)
                .joinToString(separator = ", ") { it.displayName }
    }

    @IntDef(NEW_GROUP, USER_LEFT)
    annotation class Type
}