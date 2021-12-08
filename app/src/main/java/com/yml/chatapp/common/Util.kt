package com.yml.chatapp.common

import com.yml.chatapp.data.model.DbUser

object Util {

    fun userInfoFromHashMap(user: HashMap<*,*>): DbUser {
        return DbUser(
            phoneNo = user["phoneNo"].toString(),
            name = user["name"].toString(),
            status = user["status"].toString()
        )
    }
}