package util

interface Builder<T> {
  fun build(): T
}