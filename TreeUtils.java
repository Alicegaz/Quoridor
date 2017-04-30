package quoridor;

/**
 * Created by Alice on 06.04.2017.
 */
public class TreeUtils {

    quoridor.Node rootNode = new quoridor.Node();
    double totalNumberOfBranches;
    double totalNumberOfNodes;
    double averageBranchingFactor;

    public TreeUtils(quoridor.Node startNode)
    {
        rootNode = startNode;
        totalNumberOfBranches = rootNode.childNodes.size();
        totalNumberOfNodes = 1;
        averageBranchingFactor = 0;
    }

    public void analyseAverageBranchingFactor()
    {
        for (quoridor.Node node: this.rootNode.childNodes)
        {
            if (node.childNodes.size() > 0)
            {
                this.analyseNode(node);
            }
        }

        this.averageBranchingFactor = this.totalNumberOfBranches / this.totalNumberOfNodes;

    }

    public void analyseNode(quoridor.Node node)
    {
        this.totalNumberOfNodes += 1;
        this.totalNumberOfBranches += node.childNodes.size();
        for (quoridor.Node n : node.childNodes)
        {
            if (n.childNodes.size() > 0)
                this.analyseNode(n);
        }
    }


}
