package com.yml.chatapp.common

import com.yml.chatapp.data.model.DbUser

object Util {

    fun userInfoFromHashMap(user: HashMap<*,*>): DbUser {
        return DbUser(
            phoneNo = user[PHONE_NO].toString(),
            name = user[USER_NAME].toString(),
            status = user[USER_STATUS].toString(),
            image = user[USER_IMAGE].toString(),
            firebaseToken = user[FIREBASE_TOKEN].toString()
        )
    }
}