package com.cresoty.catpospoint.remote.exception

class EstimatePointRetryableException(val code: String) :
    RuntimeException("retryable estimate-point code=$code")
