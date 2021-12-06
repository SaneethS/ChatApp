package com.yml.chatapp.common

import com.yml.chatapp.data.model.DbUser

object Util {

    fun userInfoFromHashMap(user: HashMap<*,*>): DbUser {
        return DbUser(
            user["phoneNo"].toString(),
            user["name"].toString()
        )
    }
}