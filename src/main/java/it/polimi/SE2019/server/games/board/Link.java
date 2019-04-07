package it.polimi.SE2019.server.games.board;

/**
 * 
 */
public class Link {

	private Tile tileA;
	private Tile tileB;
	private LinkType linkType;

	/**
	 * Default constructor
	 * @param tileA
	 * @param tileB
	 * @param linkType
	 */
	public Link(Tile tileA, Tile tileB, LinkType linkType) {
		this.tileA = tileA;
		this.tileB = tileB;
		this.linkType = linkType;
	}


	public Tile getTileA() {
		return tileA;
	}

	public void setTileA(Tile tileA) {
		this.tileA = tileA;
	}

	public Tile getTileB() {
		return tileB;
	}

	public void setTileB(Tile tileB) {
		this.tileB = tileB;
	}

	public LinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(LinkType linkType) {
		this.linkType = linkType;
	}
}