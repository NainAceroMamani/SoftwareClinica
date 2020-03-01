package nain.com.model

data class Specialty(val id: Int, val name: String) {
    // este metodo devulve una cadena de caracteres pero solo queremos que devuelva el nombre
    override fun toString(): String {
        return name
    }
}