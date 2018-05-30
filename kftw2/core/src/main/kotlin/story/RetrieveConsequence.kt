package story

interface RetrieveConsequence<out T>: Consequence {
  fun retrieve() : T
}