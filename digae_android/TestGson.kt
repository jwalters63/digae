import com.google.gson.Gson
data class TestInstalacion(val id: String? = null, val tipo: String)
fun main() {
    val json = """[{"id": 1, "tipo": "FACULTAD"}, {"tipo": "LABORATORIO"}]"""
    val gson = Gson()
    val list = gson.fromJson(json, Array<TestInstalacion>::class.java).toList()
    println(list)
}
