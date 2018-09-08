package com.lavaeater.kftw.behaviortree

import util.Builder

enum class NodeStatus {
  NONE,
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

class BehaviorTree(val name:String, private val interval:Long = -1L, private val rootNode: INode) {
  private var accruedTime = 0L
  private val useInterval get() = interval != -1L
  var lastStatus = NodeStatus.NONE
  /**
   *
   */
  fun tick(delta: Long) : NodeStatus {
    accruedTime += delta
    if((useInterval && accruedTime > interval) || (!useInterval)) {
      accruedTime = 0L
      this.lastStatus = rootNode.run() //Ignore result?
    } else {
      this.lastStatus = NodeStatus.NONE
    }
    return NodeStatus.RUNNING
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

fun <T: Any> behaviorTree(block: BehaviorTreeBuilder<T>.() -> Unit) : BehaviorTree = BehaviorTreeBuilder<T>().apply { block() }.build()
fun <T: Any> sequence(block: SequenceBuilder<T>.() -> Unit): Sequence = SequenceBuilder<T>().apply { block() }.build()
fun <T: Any> selector(block: SelectorBuilder<T>.() -> Unit): Selector= SelectorBuilder<T>().apply { block() }.build()
fun <T: Any> action(block: ActionBuilder<T>.() -> Unit): ActionNode<T> = ActionBuilder<T>().apply { block() }.build()
fun <T: Any> invert(block: InverterBuilder<T>.() -> Unit): InverterNode = InverterBuilder<T>().apply { block() }.build()

class BehaviorTreeBuilder<T:Any>:Builder<BehaviorTree> {
  var name: String = ""
  var interval: Long = -1
  lateinit var rootNode : INode

  fun sequenceRoot(block: SequenceBuilder<T>.() -> Unit) {
    rootNode = sequence(block)
  }
  fun selectorRoot(block: SelectorBuilder<T>.() -> Unit) {
    rootNode = selector(block)
  }
  fun inverterRoot(block: InverterBuilder<T>.() -> Unit) {
    rootNode = invert(block)
  }
  fun actionRoot(block: ActionBuilder<T>.() -> Unit) {
    rootNode = action(block)
  }
  override fun build(): BehaviorTree  = BehaviorTree(name, interval, rootNode)
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
  private lateinit var child : INode

  fun addSequence(block: SequenceBuilder<T>.() -> Unit) {
    child = sequence(block)
  }

  fun addSelector(block: SelectorBuilder<T>.() -> Unit) {
    child = selector(block)
  }

  fun addAction(block: ActionBuilder<T>.() -> Unit) {
    child = action(block)
  }

  override fun build(): InverterNode = InverterNode(name, child)
}

fun <T: Any> MutableList<INode>.addSequence(block: SequenceBuilder<T>.() -> Unit) { this.add(sequence(block)) }

fun <T: Any> MutableList<INode>.addSelector(block: SelectorBuilder<T>.() -> Unit) { this.add(selector(block)) }

fun <T: Any> MutableList<INode>.addInverter(block: InverterBuilder<T>.() -> Unit) { this.add(invert(block)) }

fun <T:Any> MutableList<INode>.addAction(block: ActionBuilder<T>.() -> Unit) { this.add(action(block)) }