package id.ak.myweather.domain.mapper

abstract class Mapper<From : Any, To : Any> {
    abstract fun map(from: From): To
    fun map(from: Collection<From>): List<To> = from.map<From, To>(::map)
}