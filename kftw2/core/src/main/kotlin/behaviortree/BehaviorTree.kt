package com.lavaeater.kftw.behaviortree

enum class NodeStatus {
  FAILURE,
  SUCCESS,
  RUNNING
}

interface INode {
  fun init()
  fun run(): NodeStatus
}

abstract class Node<T>(val blackBoard:T) : INode {
  override fun init() {
  }
}

class ActionNode<T>(blackBoard: T, val action: ()-> NodeStatus):Node<T>(blackBoard) {
  override fun run(): NodeStatus {
    return action()
  }
}

abstract class CompositeNode<T>(blackBoard: T, val children: List<INode>) : Node<T>(blackBoard)

abstract class DecoratorNode<T>(blackBoard: T, val child: INode):Node<T>(blackBoard)

class InverterNode<T>(blackBoard: T, child: INode):DecoratorNode<T>(blackBoard, child) {
  override fun run(): NodeStatus {
    val status = child.run()
    when(status){
      NodeStatus.SUCCESS -> return NodeStatus.FAILURE
      NodeStatus.FAILURE -> return NodeStatus.SUCCESS
      else -> return status
    }
  }
}

class BehaviorTree<T>(blackBoard: T, val rootNode: INode): Node<T>(blackBoard) {
  override fun init() {
    super.init()
  }

  /**
   *
   */
  override fun run(): NodeStatus {
    return rootNode.run()
  }
}

class Sequence<T>(blackBoard: T, children: List<INode>) : CompositeNode<T>(blackBoard, children) {
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

class Selector<T>(blackBoard: T, children: List<INode>) : CompositeNode<T>(blackBoard, children) {
  override fun run(): NodeStatus {
    for (child in children) {
      val status = child.run()
      if(status != NodeStatus.FAILURE)
        return status
    }

    return NodeStatus.FAILURE
  }
}
