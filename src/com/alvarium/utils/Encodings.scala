package com.alvarium.utils


/**
 * Converts an array of bytes to the corresponding
 * string hexadecimal representation
 *
 * @param data byte array of data
 * @return string hexadecimal representation
 */
def bytesToHex(data: Array[Byte]) = {
  val hexString = new StringBuilder(2 * data.length)
  for (i <- data.indices) {
    val hex = Integer.toHexString(0xff & data(i)).toUpperCase
    if (hex.length == 1) hexString.append('0')
    hexString.append(hex)
  }
  hexString.toString
}

/**
 * Converts a hex string to a byte array
 * and will return the conversion of complete bytes (i.e.
 * last value in odd-sized input will be ignored
 *
 * @param hexa Hexadecimal value in string format
 * @return byte array from the hex input
 */
def hexToBytes(hexa: String) = {
  // Remove incomplete bytes if odd length
  var hex = hexa
  var len = hex.length
  if (len % 2 != 0) hex = hex.substring(0, len - 1) // Remove last element
  len = hex.length
  val data = new Array[Byte](len / 2)
  var i = 0
  while (i < len) {
    data(i / 2) = ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16)).toByte

    i += 2
  }
  data
}