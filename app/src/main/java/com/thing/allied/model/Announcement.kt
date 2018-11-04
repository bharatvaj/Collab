package com.thing.allied.model

import java.util.*
import kotlin.collections.ArrayList


data class Announcement(
    var firebaseUid: String?,
    var title: String,
    var message: String,
    var attachments: List<String>,
    var announceDate: Date,
    var expiryDate: Date?,
    var isImportant: Boolean,
    var repliesChatMetadataUrl: String
) {
    constructor() : this("", "", "", ArrayList<String>(), Date(), Date(), false, "")
}