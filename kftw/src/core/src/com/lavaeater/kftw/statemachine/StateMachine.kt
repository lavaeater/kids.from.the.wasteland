package com.lavaeater.kftw.statemachine

/**
 * Builds and operates state machines
 */
class StateMachine<S,E> private constructor(private val initialState: S, private val globalStateAction: (S) -> Unit) {
    private lateinit var currentState: State<S,E>
    private val states = mutableListOf<State<S,E>>()

    fun state(stateName: S, init: State<S,E>.() -> Unit) {
        val state = State<S,E>(stateName)
        state.init()

        states.add(state)
    }

    /**
     * Translates state state to an object
     */
    private fun getState(state: S): State<S,E> {
        return states.firstOrNull { state == it} ?:
                throw NoSuchElementException(state.toString())
    }

    /**
     * Initializes the [StateMachine] and puts it on the first state
     */
    fun initialize() {
        currentState = getState(initialState)
        currentState.enter()
        globalStateAction(currentState.state)
    }

    /**
     * Gives the FSM an event to act upon, state is then changed and actions are performed
     */
    fun acceptEvent(e: E) {
        try {
            val edge = currentState.getEdgeForEvent(e)

            // Indirectly get the state stored in edge
            // The syntax is weird to guarantee that the states are changed
            // once the actions are performed
            // This line just queries the next state state (Class) from the
            // state list and retrieves the corresponding state object.
            val state = edge.applyTransition { getState(it) }
            state.enter()

            currentState = state
            globalStateAction(currentState.state)
        } catch (exc: NoSuchElementException) {
            throw IllegalStateException("This state doesn't support " +
                    "transition on ${e}")
        }
    }

    companion object {
        fun <S,E>buildStateMachine(initialStateName: S, globalStateAction: (S) -> Unit, init: StateMachine<S,E>.() -> Unit): StateMachine<S,E> {
            val stateMachine = StateMachine<S,E>(initialStateName, globalStateAction)
            stateMachine.init()
            return stateMachine
        }
    }
}
