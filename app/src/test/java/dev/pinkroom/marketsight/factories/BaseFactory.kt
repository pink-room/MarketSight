package dev.pinkroom.marketsight.factories

interface BaseFactory<T> {
    fun build(): T
    fun buildList(number: Int = 3) = List(number) { build() }
}