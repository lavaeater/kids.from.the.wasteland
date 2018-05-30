package story

interface ProcessInputConsequence : Consequence {
  fun <T> processInput(value: T)
}