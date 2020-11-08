package algorithms;

import java.awt.Point;

public class Sommet extends Point {
	
	enum Couleur{BLANC, GRIS, NOIR, BLEU};
	
	Couleur couleur;
	int  id;
	boolean actif=false;
	int whiteCount;
	
	public Sommet(Point arg0, int id) {
	super(arg0);
	couleur=Couleur.BLANC;
	this.id=id;
	whiteCount=0;
	
	}
	
	public void setActif(boolean b){
		actif=b;
	}

	public boolean isActif() {
		return actif;
	}
	
	
}