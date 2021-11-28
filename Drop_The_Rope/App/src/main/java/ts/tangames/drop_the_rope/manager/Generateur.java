package ts.tangames.drop_the_rope.manager;

import java.util.Random;

import ts.tangames.drop_the_rope.factory.ModeType;
import ts.tangames.drop_the_rope.object.Gem;
import ts.tangames.drop_the_rope.object.Platform;

public class Generateur {


	// ---------------------------------
	// VARIABLES
	// ---------------------------------

	// Singleton
	private final Random r = new Random();
	private ModeType mode = ModeType.NORMAL;

	private static Generateur instance;

	public static int seed = 42424243;

	// ===== Plateform position
	// points de références
	private int Y_REF=235;
	private int W_REF=110;
	private int H_REF=50;
	private int SPACING_REF=300;

	// bornes
	private int Y_MIN=215;
	private int W_MIN=80;
	private int H_MIN=45;
	private int SPACING_MIN=252;

	private int Y_MAX=300;
	private int W_MAX=130;
	private int H_MAX=55;
	private int SPACING_MAX=380;

	// historique
	private int DEBUT = 400;
	private int lastX=DEBUT,lastY=Y_REF;		// position de la dernière plateforme créée
	private int lastH=H_REF,lastW=W_REF;	// dimensions de la dernière plateforme créée
	private int lastSpacing=SPACING_REF;	// écart de la dernière plateforme créée

	// ===== Coin position
	private int lastXGem=DEBUT,lastYGem=Y_REF;	// position de la dernière gem créée
	private int lastSpacingGem=SPACING_REF;		// écart de la dernière gem créée


	// ---------------------------------
	// SINGLETON
	// ---------------------------------

	private Generateur(){
		
		r.setSeed(System.currentTimeMillis());
	}
	
	public static Generateur getInstance(){
		if(instance == null){
			instance = new Generateur();
		}
		return instance;
	}


	// ---------------------------------
	// METHODS
	// ---------------------------------

	public double nextDouble(){
		return r.nextDouble();
	}

	public int nextInt(){
		return r.nextInt();
	}
	
	/**
	 * Renvoie un entier aleatoire compris entre 0 et max (exclu)
	 * @param max
	 * @return
	 */
	public int nextInt(int max){
		return r.nextInt(max);
	}

	/**
	 * Renvoie un entier aleatoire compris entre min et max (inclus)
	 * @param min
	 * @param max
	 * @return
	 */
	public int nextInt(int min, int max){
		return (int)(r.nextDouble()*(max-min)+min);
	}

	public void reset(){

		Platform.count=0;
		Gem.count=0;

		lastX=400;
		lastY=Y_REF;
		lastH=H_REF;
		lastW=W_REF;
		lastSpacing=SPACING_REF;

		lastXGem=DEBUT;
		lastYGem=Y_REF;
		lastSpacingGem=SPACING_REF;

		r.setSeed(System.currentTimeMillis());
	}

	/**
	 *
	 * @param count le nombre de plateform déjà créées (celle-ci incluse)
	 * @return [x,y,w,h] de la nouvelle plateform en fonction du mode
     */
	public int[] nextPlateform(int count){
		switch(mode){
			case EASY:
				return nextPlateformEasyMode(count);
			case NORMAL:
				return nextPlateformNormalMode(count);
			case DIFFICULT:
				return nextPlateformDifficultMode(count);
			default:
				return null;
		}
	}

	private int[] nextPlateformEasyMode(int count){
		int next[] = new int[4];

		next[0]=lastX+nextInt(SPACING_MIN-30,SPACING_MAX-60);
		next[1]=nextInt(Y_MIN+15,Y_MAX-15);
		next[2]=nextInt(W_MIN+20,W_MAX+30);
		next[3]=nextInt(H_MIN+7,H_MAX+15);

		lastSpacing=next[0]-lastX;
		lastX=next[0];
		lastY=next[1];
		lastW=next[2];
		lastH=next[3];

		return next;
	}

	private int[] nextPlateformNormalMode(int count){
		int next[] = new int[4];

		next[0]=lastX+nextInt(SPACING_MIN,SPACING_MAX-5);
		next[1]=nextInt(Y_MIN,Y_MAX);
		next[2]=nextInt(W_MIN,W_MAX);
		next[3]=nextInt(H_MIN,H_MAX);

		lastSpacing=next[0]-lastX;
		lastX=next[0];
		lastY=next[1];
		lastW=next[2];
		lastH=next[3];

		return next;
	}

	private int[] nextPlateformDifficultMode(int count){
		int next[] = new int[4];

		next[0]=lastX+nextInt(SPACING_MIN+30,SPACING_MAX+57);
		next[1]=nextInt(Y_MIN-13,Y_MAX+13);
		next[2]=nextInt(W_MIN-23,W_MAX-30);
		next[3]=nextInt(H_MIN-7,H_MAX-15);

		lastSpacing=next[0]-lastX;
		lastX=next[0];
		lastY=next[1];
		lastW=next[2];
		lastH=next[3];

		return next;
	}

	/**
	 *
	 * @param count le nombre de gem déjà créées (celle-ci incluse)
	 * @return [x,y] de la nouvelle gem
     */
	public int[] nextGem(int count){
		switch(mode){
			case EASY:
				return nextGemEasyMode(count);
			case NORMAL:
				return nextGemNormalMode(count);
			case DIFFICULT:
				return nextGemDifficultMode(count);
			default:
				return null;
		}
	}

	public int[] nextGemEasyMode(int count){
		int next[] = new int[2];

		next[0]=lastXGem+nextInt(SPACING_MIN*4,SPACING_MAX*6);
		next[1]=nextInt(Y_MIN-125,Y_MAX-115);

		lastSpacingGem=next[0]-lastX;
		lastXGem=next[0];
		lastYGem=next[1];

		return next;
	}

	public int[] nextGemNormalMode(int count){
		int next[] = new int[2];

		next[0]=lastXGem+nextInt(SPACING_MIN,SPACING_MAX*3);
		next[1]=nextInt(Y_MIN-125,Y_MAX-115);

		lastSpacingGem=next[0]-lastX;
		lastXGem=next[0];
		lastYGem=next[1];

		return next;
	}

	public int[] nextGemDifficultMode(int count){
		int next[] = new int[2];

		next[0]=lastXGem+nextInt(SPACING_MIN/2, SPACING_MAX);
		next[1]=nextInt(Y_REF-40,Y_REF-100);

		lastSpacingGem=next[0]-lastX;
		lastXGem=next[0];
		lastYGem=next[1];

		return next;
	}


	// ---------------------------------
	// GETTERS / SETTERS
	// ---------------------------------

	public void setMode(ModeType mode) {
		this.mode = mode;
	}

	public ModeType getMode() {
		return mode;
	}

	public void setSeed(long seed){
		r.setSeed(seed);
	}
}
