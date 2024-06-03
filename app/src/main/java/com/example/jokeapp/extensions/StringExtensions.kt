// Extension function to get the substring of a string before the nth occurrence of a delimiter
fun String.substringBeforeNthDelimiter(delimiter: Char, n: Int): String{  var index = -1
    var found = 0
    // Find the nth occurrence of the delimiter
    while(found < n) {
        // Find the next occurrence of the delimiter (after the last found index)
        // "this" refers to the string on which the function is called
        index = this.indexOf(delimiter,index+1)
        // If the delimiter is not found (indexOf returns -1), break the loop
        if(index == -1) break
        // Increment the count of found delimiters
        found++
    }
    // Return the substring before the nth occurrence of the delimiter or the whole string
    // if the nth occurrence delimiter is not found
    return if(index != -1)
        this.substring(0,index)
    else
        this
}