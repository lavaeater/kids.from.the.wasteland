package com.lavaeater.kftw.behaviortree

import util.Builder

enum class NodeStatus {
  FAILURE,
  SUCCESS,
  RUNNING
}

interface INode {
  fun init()
  fun run(): NodeStatus
}

abstract class Node(val name: String) : INode {
  override fun init() {
  }
}

class ActionNode<T>(name:String, val blackBoard: T, val action: (blackBoard:T)-> NodeStatus):Node(name) {
  override fun run(): NodeStatus {
    return action(blackBoard)
  }
}

abstract class CompositeNode(name:String, val children: List<INode>) : Node(name)

abstract class DecoratorNode(name:String, val child: INode):Node(name)

class InverterNode(name:String, child: INode):DecoratorNode(name, child) {
  override fun run(): NodeStatus {
    val status = child.run()
    when(status){
      NodeStatus.SUCCESS -> return NodeStatus.FAILURE
      NodeStatus.FAILURE -> return NodeStatus.SUCCESS
      else -> return status
    }
  }
}

class BehaviorTree<T>(val name:String, val interval:Long = -1L, val rootNode: INode) {
  var accruedTime = 0L
  val useInterval get() = interval != -1L
  /**
   *
   */
  fun tick(delta: Long) {
    accruedTime += delta
    if((useInterval && accruedTime > interval) || (!useInterval)) {
      rootNode.run() //Ignore result?
      accruedTime = 0L
    }
  }
}

class Sequence(name:String, children: List<INode>) : CompositeNode(name, children) {
  override fun run(): NodeStatus {
    //Run all children in sequence until one fails... but what if one is running? Figure out later...

    for (child in children) {
      val status = child.run()
      if(status != NodeStatus.SUCCESS)
        return status
    }
    return NodeStatus.SUCCESS
  }
}

class Selector(name:String, children: List<INode>) : CompositeNode(name, children) {
  override fun run(): NodeStatus {
    for (child in children) {
      val status = child.run()
      if(status != NodeStatus.FAILURE)
        return status
    }

    return NodeStatus.FAILURE
  }
}

fun <T: Any> behaviorTree(block: BehaviorTreeBuilder<T>.() -> Unit) : BehaviorTree<T> = BehaviorTreeBuilder<T>().apply { block }.build()
fun <T: Any> sequence(block: SequenceBuilder<T>.() -> Unit): Sequence = SequenceBuilder<T>().apply { block }.build()
fun <T: Any> selector(block: SelectorBuilder<T>.() -> Unit): Selector= SelectorBuilder<T>().apply { block }.build()
fun <T: Any> action(block: ActionBuilder<T>.() -> Unit): ActionNode<T> = ActionBuilder<T>().apply { block }.build()
fun <T: Any> invert(block: InverterBuilder<T>.() -> Unit): InverterNode = InverterBuilder<T>().apply { block }.build()

class BehaviorTreeBuilder<T:Any>:Builder<BehaviorTree<T>> {
  var name: String = ""
  var interval: Long = -1
  lateinit var rootNode : INode

  override fun build(): BehaviorTree<T>  = BehaviorTree(name, interval, rootNode)
}

class SelectorBuilder<T: Any> : Builder<Selector> {
  var name: String = ""
  private val children = mutableListOf<INode>()

  fun addSequence(block: SequenceBuilder<T>.() -> Unit) = children.addSequence(block)
  fun addSelector(block: SelectorBuilder<T>.() -> Unit) = children.addSelector(block)
  fun addInverter(block: InverterBuilder<T>.() -> Unit) = children.addInverter(block)
  fun addAction(block: ActionBuilder<T>.() -> Unit) = children.addAction(block)

  override fun build(): Selector = Selector(name, children)
}

class SequenceBuilder<T: Any> : Builder<Sequence> {
  var name: String = ""
  private val children = mutableListOf<INode>()

  fun addSequence(block: SequenceBuilder<T>.() -> Unit) = children.addSequence(block)
  fun addSelector(block: SelectorBuilder<T>.() -> Unit) = children.addSelector(block)
  fun addInverter(block: InverterBuilder<T>.() -> Unit) = children.addInverter(block)
  fun addAction(block: ActionBuilder<T>.() -> Unit) = children.addAction(block)

  override fun build(): Sequence = Sequence(name, children)
}

class ActionBuilder<T:Any>() : Builder<ActionNode<T>> {
  var name: String = ""
  lateinit var blackBoard: T
  lateinit var action : (blackBoard:T) -> NodeStatus

  override fun build(): ActionNode<T> = ActionNode(name, blackBoard, action)
}

class InverterBuilder<T: Any> : Builder<InverterNode> {
  var name: String = ""
  lateinit private var child : INode

  fun addSequence(block: SequenceBuilder<T>.() -> Unit) = sequence(block)
  fun addSelector(block: SelectorBuilder<T>.() -> Unit) = selector(block)
  fun addAction(block: ActionBuilder<T>.() -> Unit) = action(block)

  override fun build(): InverterNode = InverterNode(name, child)
}

fun <T: Any> MutableList<INode>.addSequence(block: SequenceBuilder<T>.() -> Unit) = sequence(block)

fun <T: Any> MutableList<INode>.addSelector(block: SelectorBuilder<T>.() -> Unit) = selector(block)

fun <T: Any> MutableList<INode>.addInverter(block: InverterBuilder<T>.() -> Unit) = invert<T> { block }

fun <T:Any> MutableList<INode>.addAction(block: ActionBuilder<T>.() -> Unit) = action<T> { block }