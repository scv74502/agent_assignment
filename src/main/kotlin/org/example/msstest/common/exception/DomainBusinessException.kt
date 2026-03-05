package org.example.msstest.common.exception

interface DomainBusinessException {
    val errorCode: ErrorCode
    val message: String?
}
