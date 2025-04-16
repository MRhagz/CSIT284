package com.example.quetek.models.user

import com.example.quetek.models.Program
import com.example.quetek.models.UserType
import com.example.quetek.models.Window
import com.example.quetek.utils.simpleFormat
import java.util.Date

class Accountant(
    id: String,
    password: String,
    firstName: String,
    lastName: String,
    window: Window = Window.NONE,
    userType: UserType = UserType.ACCOUNTANT,
    timeServed: String = Date().simpleFormat(),
) : User(id, password, firstName, lastName, userType, timeServed) {

    constructor() : this("", "", "", "", Window.NONE, UserType.ACCOUNTANT,"")
    constructor(id: String, password: String, firstname: String, lastname: String, window: Window) : this(id, password, firstname, lastname, window, UserType.ACCOUNTANT);
}