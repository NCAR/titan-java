//  REV 4.0 for DISPLACE Rev 2.0 

#include <stdio.h>
#include "stdlib.h"
#include "string.h"
#include "ctype.h"
#include <io.h>
#include <dos.h>
#include <process.h>
#include <float.h>
#include "math.h"
#include <graphics.h>
#include "hsc.h"

/****************************************************************************/

/*--------------------------------------------------------------------------*/
/* HS related stuff --------------------------------------------------------*/
/*--------------------------------------------------------------------------*/
char Choice[3];
char ScreenType[26];
char GMode[3];
char TMode[3];
char HSKey[3+1];                            /* key pressed by user */
int Mode;

/*--------------------------------------------------------------------------*/
/*- GRAPHICS RELATED VARIABLES----------------------------------------------*/
/*--------------------------------------------------------------------------*/
int    GraphDriver;             /* The Graphics device driver           */
int    GraphMode;               /* The Graphics mode value              */
void Init_graphics(void);
void gprintf(char clr,int x,int y,char *fmt,...);

/****************************************************************************/
FILE *fp1; /* output file RCAL4_1/2.OUT  */ 
FILE *fp2; /* output file RCAL_1 or RCAL_2.DAT  */ 
FILE *fp3; /* input  file RCAL4_1/2.CFG  */ 
FILE *fp4; /* output file RCALR 1/2.DAT */ 
/****************************************************************************/
char    name[ 20 ], station[ 20 ];

struct date datep;
struct time timep;
unsigned int station_num;


float   pp, tf, bw, wl, ta, ca, A, b, radconst, ag;
float   pi, h, dec, sl, twadl, wal, phi, theta, rr1, dbz1, pr1, sg1;
float   pr[ 15 ], sg[ 15 ], rr[ 15 ];
float   tx_pw; /* transmitter pulse width */
float   atmos_atten;

float bias_corr; /* bias correction log = 2.5  lin = 1.05  quad = 0 */
char table_type = 0;
unsigned channel = 1;

/****************************************************************************/
/****************************************************************************/
main()
{
	ScreenConfiguration();
	GraphicMode();
	registerbgidriver(EGAVGA_driver);
   Init_graphics();
   intro();

	HS("OPEN,RCAL4.AID");
	display_config();

	while(strcmp(HsKey,"ESC")!=0)
	{
	  HS("SCREEN,INPUT");
     if(strcmp(HsKey,"FK0")==0)
     {
      save_config();
      display_config();
	   calc_rconst();
	  } 

	  if(strcmp(HsKey,"VIW")==0)
	  {
	   HS("CLOSE");
	   TextMode();
      splutch_up();
      if(channel == 1)system("LIST RCAL4_1.OUT"); 
      else system("LIST RCAL4_2.OUT");
		GraphicMode();
   	Init_graphics();
		HS("OPEN,RCAL4.AID");
		display_config();
     }
   }

	HS("CLOSE");
   HS ("MODE,%s",TMode);
   HS("SCROLL,UP,1,1,80,24,24"); /*       clear the screen */

}


/****************************************************************************/
intro() 
{
   char *temp1 ="00";

	HS("OPEN,RCALINTR.AID");
   HS("DISPLAY,CHAN,=%d",channel);
	HS("SCREEN,INPUT");
   HS("SCREEN,RECOVER,CHAN");
   temp1 = HsString;
   sscanf(temp1,"%u",&channel);
	splutch_down();
	HS("CLOSE");

}

/****************************************************************************/
save_config()
{

   char fname [15];

   if(channel == 1)sprintf(fname,"RCAL4_1.CFG");
   else sprintf(fname,"RCAL4_2.CFG");

	if((fp3 = fopen( fname,"wt")) == NULL)
	{
	 splutch_up();
    HS("MESSAGE,NOT ABEL TO OPEN CFG FILE --- HIT A KEY TO CONTINUE ---");
	 splutch_down();
    getch();
	}else
        {
          HS("SCREEN,RECOVER,SNUM");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,SNAME");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,UNAME");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,CHAN");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,PTP");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,THZ");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,BW");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,AG");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,OWWL");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,RDL");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,TCA");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,CA");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,PWIDTH");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,TBL_TYP");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,ZA");
			 fprintf(fp3,"%s\n",HsString);  
          HS("SCREEN,RECOVER,ZB");
			 fprintf(fp3,"%s\n",HsString);  

			 fclose(fp3);
		  }

}
/****************************************************************************/
display_config()
{

   char fname [15];

   getdate( &datep );
   gettime( &timep );

   if(channel == 1)sprintf(fname,"RCAL4_1.CFG");
   else sprintf(fname,"RCAL4_2.CFG");

	if((fp3 = fopen( fname,"rt")) == NULL)
	{
	 splutch_up();
	 HS("MESSAGE,RCAL4.CFG FILE NOT FOUND --- HIT A KEY TO CONTINUE ---");
	 splutch_down();
    getch();
	}else
        {
			 fscanf(fp3,"%02d",&station_num);/* station number */
			 fscanf(fp3,"%s",station);  /* site name */
			 fscanf(fp3,"%s",name);     /* user name */
			 fscanf(fp3,"%u",&channel); /* channel number */
			 fscanf(fp3,"%f",&pp);      /* peak power */
			 fscanf(fp3,"%f",&tf);      /* tx frequency */
			 fscanf(fp3,"%f",&bw);      /* beam width   */
			 fscanf(fp3,"%f",&ag);      /* antenna gain */
			 fscanf(fp3,"%f",&wl);      /* one way waveguide loss */
			 fscanf(fp3,"%f",&twadl);   /* two way radome loss */
			 fscanf(fp3,"%f",&ta);      /* test cable attenuation */
			 fscanf(fp3,"%f",&ca);      /* coupler attenuation    */
			 fscanf(fp3,"%f",&tx_pw);   /* pulse width */
			 fscanf(fp3,"%u",&table_type);/* displace table type in use */
			 fscanf(fp3,"%f",&A);       /* zr A */
			 fscanf(fp3,"%f",&b);       /* zr b */
			 fclose(fp3);
		  }

   /* fix for Y2K */
   if(datep.da_year < 2000)HS("DISPLAY,CYY,=%2d",(datep.da_year-1900));
   else HS("DISPLAY,CYY,=%2d",(datep.da_year-2000));
	HS("DISPLAY,CMM,=%2d",datep.da_mon);
	HS("DISPLAY,CDD,=%2d",datep.da_day);

	HS("DISPLAY,CHH,=%02d",timep.ti_hour);
	HS("DISPLAY,CMI,=%02d",timep.ti_min);

	HS("DISPLAY,SNUM,=%02d",station_num);
	HS("DISPLAY,SNAME,=%s",station);
	HS("DISPLAY,UNAME,=%s",name);
	HS("DISPLAY,CHAN,=%u",channel);
	HS("DISPLAY,PTP,=%f",pp);
	HS("DISPLAY,THZ,=%f",tf);
	HS("DISPLAY,BW,=%f",bw);
	HS("DISPLAY,AG,=%f",ag);
	HS("DISPLAY,OWWL,=%f",wl);
	HS("DISPLAY,RDL,=%f",twadl);
	HS("DISPLAY,TCA,=%f",ta);
	HS("DISPLAY,CA,=%f",ca);
	HS("DISPLAY,PWIDTH,=%f",tx_pw);
	HS("DISPLAY,TBL_TYP,=%u",table_type);
	HS("DISPLAY,ZA,=%f",A);
	HS("DISPLAY,ZB,=%f",b);

}

/****************************************************************************/
calc_rconst()
{
    char fname [15];
    char fname2 [15];
    char fname3 [15];

	 unsigned int i;

   // Sort out the two way atmospheric attenuation for the appropriate frequency
    if(tf>2.0 && tf<=4.0)       atmos_atten = 1.2;
    else if(tf>4.0 && tf<=8.0)  atmos_atten = 1.4;
    else if(tf>8.0 && tf<=12.5) atmos_atten = 2.4;
    else atmos_atten = 0;

    if(channel == 1)sprintf(fname3,"RCAL4_1.OUT");
    else sprintf(fname3,"RCAL4_2.OUT");
	 if((fp1 = fopen( fname3, "w" ) ) == NULL )
	 {
		printf( "RCAL4 File OPEN error\n" );
		exit( 1 );
	 }

    if(channel == 1)sprintf(fname,"RCAL_1.DAT");
    else sprintf(fname,"RCAL_2.DAT");
	 if( (fp2 = fopen( fname, "w" ) ) == NULL )
    {
      printf( "RCAL File OPEN error\n" );
      exit( 1 );
    }

    if(channel == 1)sprintf(fname2,"RCALR_1.DAT");
    else sprintf(fname2,"RCALR_2.DAT");
	 if( (fp4 = fopen( fname2, "w" ) ) == NULL )
    {
      printf( "RCALR File OPEN error\n" );
      exit( 1 );
    }


    getdate( &datep );
    gettime( &timep );

	 fprintf( fp1,"DATE:%04d/%02d/%02d \n",datep.da_year,datep.da_mon,datep.da_day);
	 fprintf( fp1,"TIME:%02d:%02d \n",timep.ti_hour,timep.ti_min);
	 fprintf( fp1, "\nStation : %s", station );
	 fprintf( fp1, "\nTech    : %s", name );
	 fprintf( fp1, "\nChannel : %u\n",channel);
	 fprintf( fp1, "\nPeak transmitter power   : %04.2f", pp );
	 fprintf( fp1, "\nTransmitter frequency    : %05.3f", tf );
	 fprintf( fp1, "\nAntenna gain             : %04.1f", ag );
	 fprintf( fp1, "\nBeamwidth, degrees       : %03.1f", bw );
	 fprintf( fp1, "\nOne-way waveguide loss   : %03.1f", wl );
	 fprintf( fp1, "\nTwo way Radome Loss      : %03.1f", twadl );
	 fprintf( fp1, "\nTest cable attenuation   : %03.1f", ta );
	 fprintf( fp1, "\nCoupler attenuation      : %05.2f", ca );
	 fprintf( fp1, "\nTransmitter pulse width  : %05.2f", tx_pw );
	 fprintf( fp1, "\nZR relation constants    : A = %03.0f  b = %03.1f", A, b );
	 fprintf( fp1, "\nDISPLACE Table type      : %02u",table_type);
		
//  Determine the radarconst. from Batten radar equ. constants
	 pi    = (float)(pow( 3.1415927, 3.0 ));  // pi to the third
	 h     = tx_pw*300.0;                     // pulse length in space (2us pulse duration=600.0 )
	 dec   = (float)(9.3 * pow( 10.0, -1.0 ));// dielectric constant for waterdrop squared
	 sl    = (float)(3.0 * pow( 10.0, 8.0 )); // speed of light (m/s)
//  twadl = 0.25 * 2.0;                      // two wave radome loss
    wal   = (float)(pow( ( sl / (tf * pow( 10.0, 9.0 )) ), 2.0 )); //  wavelength squared
    ag    = (float)(pow( (pow( 10.0, (double)(ag/10.0) )), 2.0 )); // antenna gain in mW squared
	 phi   = bw / 57.296;                                           // beamwidth in rad
    theta = bw / 57.296;

//  radarconstant

    radconst = 10.0 * (float)( log10( (double)( (pi*h*ag*dec*phi*theta*pow( 10.0, -18.0 )) / (1024.0 * 0.693 * wal) ) )) - ( wl*2.0 ) - twadl;

//	 printf( "\n\nRadar constant for range in metres = %10.5f", radconst );
	 fprintf( fp1, "\n\nRadar constant for range in metres = %10.5f", radconst );

//  printf( "\nRadar constant for range in nm     = %10.5f", radconst-65.36 );
    fprintf( fp1, "\nRadar constant for range in nm     = %10.5f", radconst-65.36 );


    // write to RCALR.DAT file 
	 fprintf( fp4, "%02d\n",station_num); 
    fprintf( fp4, "%s\n",station); 
	 fprintf( fp4, "%10.5f\n",radconst-65.36); 
	 fprintf( fp4, "%4.3f\n",atmos_atten/100.0); 
	 fprintf( fp4, "%4.2f\n",pp); 
    fprintf( fp4, "%u\n",table_type); 



//  modified transmitter power

//  printf( "\n\nSignal Generator delay of  641.7us  followed by a  50us  pulse." );
//	 printf( "\nThe following applies at  100km :" );
	 fprintf( fp1, "\n\nSignal Generator delay of  641.7us  followed by a  50us  pulse." );
	 fprintf( fp1, "\nThe following applies at  100km :" );

	 if(table_type == 0)bias_corr = 2.50; /* log  */
	 if(table_type == 1)bias_corr = 1.05; /* lin  */
	 if(table_type == 2)bias_corr = 0.0;  /* quad */

    rr1 = 1.0;
    dbz1 = (float)(10.0 * log10( (double)(A*pow( (double)rr1, 1.6 )) ));
    pr1 = dbz1 + radconst + pp - 100.0 - atmos_atten;    // 2-way atmospheric attenuation of 1.45db per 100km C band
    sg1 = pr1 + ( ta+ca ) - bias_corr;			 

//	 printf( "\n\n Rainrate      dBZ     Received Power   Signalgen Setting" );
//	 printf( "\n   mm/h                     dBm               dbm" );
//	 printf( "\n%7.2f       %4.1f       %7.2f            %6.2f\n", 
//				 rr1, dbz1, pr1, sg1 );
	 fprintf( fp1, "\n\n Rainrate      dBZ     Received Power   SignalGen Setting" );
	 fprintf( fp1, "\n   mm/h                     dBm               dBm" );
	 fprintf( fp1, "\n%7.2f       %4.1f       %7.2f            %6.2f\n", 
				 rr1, dbz1, pr1, sg1 );



	 for  ( i = 0; i < 15; i++ )
	 {
		  pr[ i ] = ( i*5.0 ) + radconst + pp - 100.0 - atmos_atten;    // 2-way atmospheric attenuation of 1.45db per 100km C band
		  sg[ i ] = pr[ i ] + ( ta+ca ) - bias_corr;
		  rr[ i ] =	(float)( pow( (double)( pow( 10.0, (double)( ( i*5.0 ) / 10.0 )) / A ), (double)(1.0/b)));
//		  printf( "\n%7.2f       %4.1f       %7.2f            %6.2f", 
//					  rr[ i ], ( i*5.0 ), pr[ i ], sg[ i ] );
		  fprintf( fp1, "\n%7.2f       %4.1f       %7.2f            %6.2f", 
					  rr[ i ], ( i*5.0 ), pr[ i ], sg[ i ] );


		  // write to RCAL_1 or RCAL_2.DAT file depending on selection
		  //**********************************************************
		  fprintf( fp2, "%4.1f\n",sg[i] ); 
		  fprintf( fp2, "%02.0f\n",(i*5.0) ); 


		  // write to RCALR.DAT file 
		  //************************
		  fprintf( fp4, "%02.0f\n",(i*5.0) ); 
		  fprintf( fp4, "%4.2f\n",pr[i] ); 


	 }


	 fclose( fp1 );
	 fclose( fp2 );
	 fclose( fp4 );

}

/***************************************************************************/
ScreenConfiguration()
{
   HS ("MODE,CONFIG");
   Mode=HsInt;
   if (Mode > 64)  Mode = Mode - 64;
   switch (Mode) {
   case 6:  {
            strcpy(ScreenType,"CGA"); 
            strcpy(GMode,"3"); 
            strcpy(TMode,"3");
            break;
            };
   case 7:  {
            strcpy(ScreenType,"MONOCHROME"); 
            strcpy(GMode,"7"); 
            strcpy(TMode,"7");
            break;
            };
   case 8:  {
            strcpy(ScreenType,"HERCULES"); 
            strcpy(GMode,"8"); 
            strcpy(TMode,"7");
            break;
            };
   case 15: {
            strcpy(ScreenType,"HERCULES EMULATING EGA");
            strcpy(GMode,"16"); 
            strcpy(TMode,"7");
            break;
            };
   case 16: {
            strcpy(ScreenType,"EGA"); 
            strcpy(GMode,"16"); 
            strcpy(TMode,"3");
            break;
            };
   case 19: {
            strcpy(ScreenType,"VGA"); 
            strcpy(GMode,"16"); 
            strcpy(TMode,"3");
            break;
            };
	};

            strcpy(ScreenType,"VGA"); 
            strcpy(GMode,"16"); 
            strcpy(TMode,"3");

};

/***************************************************************************/
GraphicMode()
{
  HS("MODE,%s",GMode);                       /* pass to GMode */
}

/***************************************************************************/
TextMode()
{
  HS("MODE,%s",TMode);                       /* pass to TMode */
}

/***************************************************************************/
void gprintf(char clr,int x,int y,char *fmt,...)
{
 int wd,ht;
 va_list  argptr;			/* Argument list pointer	*/
 char str[140];			/* Buffer to build sting into	*/

 va_start( argptr, fmt );		/* Initialize va_ functions	*/

 vsprintf( str, fmt, argptr );	/* prints string to buffer	*/
 ht = textheight("H");
 wd = textwidth(str);

 setfillstyle(SOLID_FILL,clr);
 bar(x,y,x+wd,y+ht);

 outtextxy( x, y, str );	/* Send string in graphics mode */
 va_end( argptr );			/* Close va_ functions		*/

}

/***************************************************************************/
void Init_graphics(void)
{
	 GraphDriver = VGA;
	 GraphMode = VGAHI;
    initgraph(&GraphDriver, &GraphMode,"");
}

/***************************************************************************/
splutch_up()
{
 unsigned int i;
 for(i=100;i<10000;i++)
 {
  sound(i);
 }
  nosound();
}

splutch_down()
{
 unsigned int i;
 for(i=10000;i>100;i--)
 {
  sound(i);
 }
 nosound();
}

/***************************************************************************/
beeper(unsigned int buz)
{
 sound(buz);
 delay(40);
 nosound();
}

/***************************************************************************/
