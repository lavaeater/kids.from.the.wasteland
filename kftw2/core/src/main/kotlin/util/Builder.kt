package util

interface Builder<out T> {
	fun build(): T
}