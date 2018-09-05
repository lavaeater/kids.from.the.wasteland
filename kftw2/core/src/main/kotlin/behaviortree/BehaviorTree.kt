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

abstract class LeafNode<T>(blackBoard: T):Node<T>(blackBoard)

abstract class CompositeNode<T>(blackBoard: T, val children: List<INode>) : Node<T>(blackBoard)

abstract class DecoratorNode<T>(blackBoard: T, val child: INode):Node<T>(blackBoard)

class Sequence<T>(blackBoard: T, children: List<INode>) : CompositeNode<T>(blackBoard, children) {
  var currentIndex = 0;

  override fun run(): NodeStatus {
    if( currentIndex in 0 until children.size) {
      currentIndex++ //increment AFTER execution!
    }
    return NodeStatus.RUNNING
  }
}

class Selector<T>(blackBoard: T, children: List<INode>) : CompositeNode<T>(blackBoard, children) {
  override fun run(): NodeStatus {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}
