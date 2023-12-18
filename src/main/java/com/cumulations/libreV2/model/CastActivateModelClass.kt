package com.cumulations.libreV2.model

data class CastActivateModelClass(
    val action: String,
    val crash_report: Boolean,
    val device_uuid: String,
    val id: String,
    val request_type: String,
    val status: String,
    val status_msg: String,
    val tos: String,
    val user_ip: String
)
