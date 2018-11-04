package com.thing.allied.model

data class User(
    var firebaseUid: String,
    var id: String,
    var name: String,
    var points: Int,
    var displayImage: String,
    var summary: String,
    var gender: Gender,
    var branch: String,
    var contacts: ArrayList<String>,
    var inboxes: List<String>
) {
    constructor() : this("", "", "", 0, "", "", Gender.OTHER, "", ArrayList<String>(), ArrayList<String>())
}