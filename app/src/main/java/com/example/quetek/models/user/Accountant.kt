package com.example.quetek.models.user

import com.example.quetek.models.Program
import com.example.quetek.models.UserType
import com.example.quetek.models.Window
import java.util.Date

class Accountant(
    id: String,
    password: String,
    firstName: String,
    lastName: String,
    var window: Window = Window.NONE,
    userType: UserType = UserType.ACCOUNTANT,
    isPriority : Boolean
) : User(id, password, firstName, lastName, userType, isPriority ) {

    constructor() : this("", "", "", "", Window.NONE, UserType.ACCOUNTANT, false)
    constructor(id: String, password: String, firstname: String, lastname: String, window: Window, isPriority: Boolean) : this(id, password, firstname, lastname, window, UserType.ACCOUNTANT, isPriority)
}