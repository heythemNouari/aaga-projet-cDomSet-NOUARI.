package algorithms;

import java.awt.Point;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import algorithms.Sommet.Couleur;

public class DefaultTeam {
	
	public static int edgeThreshold;
	
	
	public ArrayList<Point> calculConnectedDominatingSet(ArrayList<Point> points, int edgeThreshold) {
		this.edgeThreshold = edgeThreshold;
		
		ArrayList<Point> mis = MIS(points);
		ArrayList<Point> res = algoArticle(mis, points);
		
		
		return res;
	}
	private ArrayList<Point> MIS(ArrayList<Point> points) {
		ArrayList<Sommet> V = new ArrayList<>();
		
		ArrayList<Point> mis = new ArrayList<>();
		int i=0;

		for(Point p: points){
			Sommet v=new Sommet(p, i++);
			V.add(v);
		}

		Sommet s = V.get(0);
		s.couleur=Couleur.NOIR;
		mis.add(s);
		for(Sommet u:voisins(s, V, edgeThreshold)){
			u.couleur=Couleur.GRIS;

			ArrayList<Sommet> tmp = voisins(u, V, edgeThreshold);
			for(Sommet p:tmp){
				p.setActif(true);
			}
		}
		while( (s=existeBlanc(V))!=null ){

			Sommet actifMax=null ;
			for(Sommet u: V){
				if(u.couleur==Couleur.BLANC && u.isActif()){ 
					actifMax = u;
					break;
				}
			}
			if(actifMax==null) continue;
			for(Sommet u: V){
				if(u.actif && u.couleur==Couleur.BLANC){
					if(voisinBlanc(u,V).size() > voisinBlanc(actifMax,V).size()){
						actifMax=u;
					}
				}
			}
			actifMax.couleur=Couleur.NOIR;
			mis.add(actifMax);
			s=actifMax;

				for(Sommet u:voisins(s, V, edgeThreshold)){
					u.couleur=Couleur.GRIS;
	
					ArrayList<Sommet> tmp = voisins(u, V, edgeThreshold);
					for(Sommet p:tmp){
						p.setActif(true);
					}
			}
		}
		return mis;
		
	}
	public ArrayList<Sommet> voisins(Sommet p, ArrayList<Sommet> vertices, int edgeThreshold){
		ArrayList<Sommet> result = new ArrayList<Sommet>();

		for (Sommet point:vertices) {
			if (point.distance(p)<edgeThreshold && !point.equals(p)) 
				result.add(point);
		}

		return result;
	}
	private ArrayList<Sommet> voisinBlanc(Sommet u, ArrayList<Sommet> v) {
		ArrayList<Sommet> neib = new ArrayList<>();
		for(Sommet r:voisins(u, v, edgeThreshold)){
			if(r.couleur==Couleur.BLANC) neib.add(r);
		}
		return neib;
	}

	private Sommet existeBlanc(ArrayList<Sommet> V) {
		for(Sommet v:V){
			if(v.couleur==Couleur.BLANC) return v;
		}
		return null;
	}
	public ArrayList<Point> algoArticle(ArrayList<Point> MIS, ArrayList<Point> points){

		Map<Integer, Set<Sommet>> comps = new HashMap<>();
		Integer compId = 0;
		ArrayList<Sommet> vs = new ArrayList<Sommet>();
		

		for(Point p: points){
			if(!MIS.contains(p)){
				Sommet g = new Sommet(p,0);
				g.couleur=Couleur.GRIS;
				vs.add(g);
			}
			else{
				Sommet b = new Sommet(p,0);
				b.couleur=Couleur.NOIR;
				vs.add(b);
			}
		}
		
		
		for(compId = 0; compId < vs.size(); compId++){
			if(vs.get((int)compId).couleur == Couleur.NOIR){
				Set<Sommet> s = new HashSet<>();
				s.add(vs.get((int)compId));
				comps.put(compId, s);
			}
		}

		for(int i = 5; i > 1; i--){
			boolean existeGris;
			do{
				existeGris = false;
				for(Sommet g : vs){
					if(g.couleur == Couleur.GRIS){
						Set<Integer> foundComp = new HashSet<>();
						for(Sommet b : voisins(g, vs, edgeThreshold)){
							if(b.couleur == Couleur.NOIR){
								foundComp.add(getComp(comps, b));
							}
						}
						if(foundComp.size() >= i){
							existeGris = true;
							g.couleur = Couleur.BLEU;
							Set<Sommet> newSet = new HashSet<Sommet>();
							for(Integer c : foundComp){
								newSet.addAll(comps.get(c));
								comps.remove(c);
							}
							comps.put(++compId, newSet);
						}
					}
				}
			}while(existeGris);
		}
	
		return sommettoPoint(vs);

	}

	public int getComp(Map<Integer, Set<Sommet>> comp, Sommet v){
		for(Integer key : comp.keySet()){
			if(comp.get(key).contains(v))
				return key;
		}
		
		return -1;
	}
	public ArrayList<Point> sommettoPoint(ArrayList<Sommet> V) {
		ArrayList<Point> ds = new ArrayList<>();

		for(Sommet v : V){
			if(v.couleur==Couleur.NOIR || v.couleur==Couleur.BLEU ){
				ds.add(new Point(v.x, v.y));
				//ds.add(v);
			}
				
		}
		return ds;
	}
	private void saveToFile(String filename,ArrayList<Point> result){
		int index=0;
		try {
			while(true){
				BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename+Integer.toString(index)+".points")));
				try {
					input.close();
				} catch (IOException e) {
					System.err.println("I/O exception: unable to close "+filename+Integer.toString(index)+".points");
				}
				index++;
			}
		} catch (FileNotFoundException e) {
			printToFile(filename+Integer.toString(index)+".points",result);
		}
	}
	private void printToFile(String filename,ArrayList<Point> points){
		try {
			PrintStream output = new PrintStream(new FileOutputStream(filename));
			int x,y;
			for (Point p:points) output.println(Integer.toString((int)p.getX())+" "+Integer.toString((int)p.getY()));
			output.close();
		} catch (FileNotFoundException e) {
			System.err.println("I/O exception: unable to create "+filename);
		}
	}

	//FILE LOADER
	public static ArrayList<Point> readFromFile(String filename) {
		String line;
		String[] coordinates;
		ArrayList<Point> points=new ArrayList<Point>();
		try {
			BufferedReader input = new BufferedReader(
					new InputStreamReader(new FileInputStream(filename))
					);
			try {
				while ((line=input.readLine())!=null) {
					coordinates=line.split("\\s+");
					points.add(new Point(Integer.parseInt(coordinates[0]),
							Integer.parseInt(coordinates[1])));
				}
			} catch (IOException e) {
				System.err.println("Exception: interrupted I/O.");
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					System.err.println("I/O exception: unable to close "+filename);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Input file not found.");
		}
		return points;
	}
}

