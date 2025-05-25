package com.konformist.comicsreader.exceptions

class ValidationException(description: String) : Exception("Invalid field: $description")
