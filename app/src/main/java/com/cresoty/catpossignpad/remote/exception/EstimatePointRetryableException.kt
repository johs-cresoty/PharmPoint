package com.cresoty.catpossignpad.remote.exception

class EstimatePointRetryableException(val code: String) :
    RuntimeException("retryable estimate-point code=$code")
