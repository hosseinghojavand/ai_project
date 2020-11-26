package com.hossein.ghojavand;

import java.util.ArrayList;
import java.util.List;

public class Tree<T>{
    private T data = null;
    private List<Tree> children = new ArrayList<>();
    private Tree parent = null;

    public Tree(T data) {
        this.data = data;
    }
    public Tree() {}

    public void addChild(Tree child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void addChild(T data) {
        Tree<T> newChild = new Tree<>(data);
        this.addChild(newChild);
    }

    public void addChildren(List<Tree> children) {
        for(Tree t : children) {
            t.setParent(this);
        }
        this.children.addAll(children);
    }

    public List<Tree> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void setParent(Tree parent) {
        this.parent = parent;
    }

    public Tree getParent() {
        return parent;
    }

    public boolean hasNode(Node node)
    {
        if (this.data.equals(node))
            return true;
        else
        {
            if (this.children.size() !=0)
            {
                for (int i = 0 ; i < this.children.size() ; i++)
                {
                    if (this.children.get(i).hasNode(node))
                        return true;
                }
            }
            return false;
        }
    }
}