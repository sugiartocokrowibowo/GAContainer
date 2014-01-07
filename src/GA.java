
public class GA {

	int numGenerasi					= 100;
	int numIndividu					= 60;
	int numIndividuTerseleksi		= 3;
	double probabilitasMutasi		= 0.50;
	
	int panjangContainer			= 10;
	int lebarContainer				= 10;
	
	int numBox						= 100;
	int[][]boxes					= new int[numBox][2];
	
	private void initializeBox(){
		int low	= 1;
		int up	= lebarContainer;//(int)(0.5*lebarContainer);
		for(int i=0;i<boxes.length;i++){
			int p	= low + (int) ( Math.random()*(1+up-low));
			int l	= low + (int) ( Math.random()*(1+up-low));
			boxes[i][0]	= p;
			boxes[i][1]	= l;
		}
	}
	
	private int[]createIndividu(){
		int[]individu	= new int[boxes.length];
		for(int i=0;i<individu.length;i++){
			int low		= 1;
			int up		= numBox;
			int alele	= low + (int) ( Math.random()*(1+up-low));
			boolean next	= false;
			while(!next){
				next	= true;
				for(int j=0;j<i;j++){
					if(individu[j]==alele){
						next=false;
						break;
					}
				}
				if(next){
					individu[i]	= alele;
					//System.out.println(-1+individu[i]);
				}else{
					alele	= low + (int) ( Math.random()*(1+up-low));
				}
			}
		}
		return individu.clone();
	}
	
	private int[][] initializeGenerasiAwal(){
		int[][]generasiAwal	= new int[numIndividu][numBox];
		for(int i=0;i<generasiAwal.length;i++){
			generasiAwal[i]=createIndividu();
		}
		return generasiAwal.clone();
	}
	
	private int[][] fillContainer(int[]individu){
		int[][]container	= new int[panjangContainer][lebarContainer];
		boolean full		= false;
		int i				= 0;
		while((!full)&&(i<numBox)){
			//cari koordinat kosong
			int x	= -1;
			int y	= -1;
			for(int k=0;k<container.length;k++){
				for(int l=0;l<container[k].length;l++){
					if(container[k][l]==0){
						x	= k;
						y	= l;
						break;
					}
				}
				if((x!=-1)&&(y!=-1)){
					break;
				}
			}
			if((x==-1)&&(y==-1)){
				full	= true;
			}else if((x!=-1)&&(y!=-1)){
				//ambil box
				int id		= -1+individu[i];
				//System.out.println(id);
				int pBox	= boxes[id][0];
				int lBox	= boxes[id][1];
				System.out.println(id+" | "+pBox+" | "+lBox);
				//periksa titik kosong seukuran box
				if(((x+pBox)<=panjangContainer)&&((y+lBox)<=lebarContainer)){
					int luas0	= pBox*lBox;
					int luas1	= 0;
					for(int k=x;k<(x+pBox);k++){
						for(int l=y;l<(y+lBox);l++){
							if(container[k][l]==0){
								luas1++;
							}
						}
					}
					if(luas1<luas0){
						//blok cell dengan nilai -1
						container[x][y]	= -1;
					}else if(luas1==luas0){
						//lakukan pengisian box ke kontainer jika ada ruang kosong seukuran box
						for(int k=x;k<(x+pBox);k++){
							for(int l=y;l<(y+lBox);l++){
								if(container[k][l]==0){
									container[k][l]	= individu[i];
								}
							}
						}
						i++;
						//bersihkan kontainer dari blokade -1
						for(int k=0;k<container.length;k++){
							for(int l=0;l<container[k].length;l++){
								if(container[k][l]==-1){
									container[k][l]	= 0;
								}
							}
						}				
					}
				}else{
					container[x][y]	= -1;
				}
			}
		}
		//bersihkan kontainer dari blokade -1
		for(int k=0;k<container.length;k++){
			for(int l=0;l<container[k].length;l++){
				if(container[k][l]==-1){
					container[k][l]	= 0;
				}
			}
		}
		return container.clone();
	}
	
	private int hitungNilaiFitness(int[][] representasi){
		int fitness	= 0;
		for(int i=0;i<representasi.length;i++){
			for(int j=0;j<representasi[i].length;j++){
				if(representasi[i][j]>0){
					fitness++;
				}
			}
		}
		return fitness;
	}
	
	public static void main(String[] args) {
		GA ga	= new GA();
		ga.initializeBox();
		int[][]generasi	= ga.initializeGenerasiAwal();
		int[]fitness	= new int[generasi.length];
		//sorting generasi awal
		//hitung fitness generasi
		int[] noUrut	= new int[generasi.length];
		for(int i=0;i<generasi.length;i++){
			int[][]container1	= ga.fillContainer(generasi[i].clone());
			int nilaiFitness	= ga.hitungNilaiFitness(container1);
			fitness[i]			= nilaiFitness;
			noUrut[i]			= i;
		}
		//sort nilai fitness
		for(int i=0;i<(-1+generasi.length);i++){
			for(int j=(1+i);j<generasi.length;j++){
				if(fitness[i]<fitness[j]){
					//tukar
					int ft		= fitness[i];
					int nt		= noUrut[i];
					fitness[i]	= fitness[j];
					noUrut[i]	= noUrut[j];
					fitness[j]	= ft;
					noUrut[j]	= nt;
				}
			}
		}
		//rekonfigurasi urutan individu berdasarkan nilai fitness
		int[][]generasi1	= new int[generasi.length][generasi[0].length];
		int[]fitness1		= fitness.clone();
		for(int i=0;i<generasi.length;i++){
			generasi1[i]	= generasi[noUrut[i]].clone();
		}
		generasi			= generasi1.clone();
		fitness				= fitness1.clone();
		int[] individuElit	= new int[ga.numBox];
		int fitnessElit=0;
		for(int g=0;g<ga.numGenerasi;g++){		
			System.out.println("generasi: "+g);
			//elitism
			if(fitness[0]>fitnessElit){
				fitnessElit		= fitness[0];
				individuElit	= generasi[0].clone();
			}
			//proses seleksi
			int[][]generasi2	= new int[generasi.length][generasi[0].length];
			int[]fitness2		= new int[generasi.length];
			for(int i=0;i<ga.numIndividuTerseleksi;i++){
				generasi2[i]	= generasi[i].clone();
				fitness2[i]		= fitness[i];
			}
			//proses crossover
			int k=ga.numIndividuTerseleksi;
			while(k<ga.numIndividu){				 
				int low		= 0;
				int up		= (generasi.length);
				int ip1		= low + (int) ( Math.random()*(up-low));
				int ip2		= low + (int) ( Math.random()*(up-low));
				while(ip1==ip2){
					ip2	= low + (int) ( Math.random()*(up-low));
				}
				int titikCrossover	= (int) ( Math.random()*(ga.numBox));
				int[]p1		= generasi[ip1].clone();
				int[]p2		= generasi[ip2].clone();
				int[]c1		= p1.clone();//new int[ga.numBox];
				int[]c2		= p2.clone();//new int[ga.numBox];
				for(int i=0;i<titikCrossover;i++){
					c1[i]	= p2[i];
					c2[i]	= p1[i];
				}
				if(k<ga.numIndividu){
					generasi2[k]	= c1.clone();
					k++;
				}
				if(k<ga.numIndividu){
					generasi2[k]	= c2.clone();
					k++;
				}
			}
			//proses mutasi
			for(int i=0;i<ga.numIndividu;i++){
				double rm	= Math.random()%1.00;
				if(rm<ga.probabilitasMutasi){
					//lakukan mutasi
					int titikMutasi	= (int) ( Math.random()*(ga.numBox));
					int low		= 1;
					int up		= ga.numBox;
					int newValue= low + (int) ( Math.random()*(1+up-low));
					generasi2[i][titikMutasi]	= newValue;
				}
			}
			//validasi alele duplikat yang kemungkinan ditimbulkan oleh proses crossover dan mutasi
			for(int i=0;i<ga.numIndividu;i++){
				for(int j=0;j<ga.numBox;j++){
					for(int h=0;h<j;h++){
						if(generasi2[i][j]==generasi2[i][h]){
							//ganti nilai duplikat
							int low		= 1;
							int up		= ga.numBox;
							int alele	= low + (int) ( Math.random()*(1+up-low));
							boolean next	= false;
							while(!next){
								next	= true;
								for(int m=0;m<i;m++){
									if(generasi2[i][m]==alele){
										next=false;
										break;
									}
								}
								if(next){
									generasi2[i][j]	= alele;
									System.out.println(-1+generasi2[i][j]);
								}else{
									alele	= low + (int) ( Math.random()*(1+up-low));
								}
							}
						}
					}
				}
			}
			int[][]generasi3= generasi2.clone();
			int[]fitness3	= new int[generasi.length];
			//sorting generasi awal
			//hitung fitness generasi
			int[] noUrut3	= new int[generasi.length];
			for(int i=0;i<generasi.length;i++){
				int[][]container3	= ga.fillContainer(generasi3[i].clone());
				int nilaiFitness	= ga.hitungNilaiFitness(container3);
				fitness3[i]			= nilaiFitness;
				noUrut3[i]			= i;
			}
			//sort nilai fitness
			for(int i=0;i<(-1+generasi.length);i++){
				for(int j=(1+i);j<generasi.length;j++){
					if(fitness3[i]<fitness3[j]){
						//tukar
						int ft		= fitness3[i];
						int nt		= noUrut3[i];
						fitness3[i]	= fitness3[j];
						noUrut3[i]	= noUrut3[j];
						fitness3[j]	= ft;
						noUrut3[j]	= nt;
					}
				}
			}
			//rekonfigurasi urutan individu berdasarkan nilai fitness
			int[][]generasi4	= new int[generasi.length][generasi[0].length];
			int[]fitness4		= fitness3.clone();
			for(int i=0;i<generasi.length;i++){
				generasi4[i]	= generasi3[noUrut[i]].clone();
			}
			generasi			= generasi4.clone();
			fitness				= fitness4.clone();
		}
		
		int[] individuTerbaik	= generasi[0].clone();
		int fitnessTerbaik		= fitness[0];
		if(fitnessTerbaik<fitnessElit){
			fitnessTerbaik		= fitnessElit;
			individuTerbaik		= individuElit.clone();
		}
		System.out.println("Fitness Terbaik: "+(fitnessTerbaik));

	}

}
