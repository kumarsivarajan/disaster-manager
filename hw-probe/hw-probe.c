#include "LCD4x40.h"
#include "uart.h"
#include <stdlib.h>
#include <string.h>

#define CMD_BUFF_LEN 20
//TODO: ciągi znakowe do pamięci flash przenieść

int debugmode;

void err(char *msg)
{
	LCD_WriteText("Blad: [");
	LCD_WriteText(msg);
	LCD_WriteText("]");
}

int main()
{
	LCD_Initalize();
	LCD_Clear();
	uart_init();

	DDRD &= ~(1 << 7);
	debugmode = (~PIND & (1 << 7)) > 0;

	if (debugmode)
		LCD_WriteText("Czujnik gotowy (tryb debug)");
	else
		LCD_WriteText("Czujnik gotowy");
	LCD_GoTo(0, 1);
	
	char cmdbuff[CMD_BUFF_LEN + 1]; // +1 do \0 dla LCD_WriteText
	
	DDRA = 255; // do zapisu (port wyjściowy)
	PORTA = 255;
	DDRB = 0;
	
	while(1)
	{
		_delay_ms(50);
		
		int read = uart_read(cmdbuff, 50);
		if (read == 0)
		{
//			LCD_WriteText("-");
			_delay_ms(250);
			continue;
		}
		
		if (debugmode)
		{
			cmdbuff[read] = '\0';
			LCD_WriteText("[");
			LCD_WriteText(cmdbuff);
			LCD_WriteText("]");
		}
		
		if (strncmp(cmdbuff, "HELLO DM-HW-PROBE", 17) == 0)
			uart_write("WELCOME DM-MASTER\n");
		else if (strncmp(cmdbuff, "SETPORT ", 8) == 0)
		{
			if (cmdbuff[8] < '0' || cmdbuff[8] > '7')
			{
				err("zly numer portu");
				continue;
			}
			int port = cmdbuff[8] - '0';
			if (cmdbuff[9] != ' ' || (cmdbuff[10] != '1' && cmdbuff[10] != '0'))
			{
				err("zly format");
				continue;
			}
			int on = (cmdbuff[10] == '1');
			
			if (on)
				PORTA &= ~(1 << port);
			else
				PORTA |= (1 << port);
			
			uart_write("AS YOU WISH, MASTER\n");
		}
		else if (strncmp(cmdbuff, "GETPORT ", 8) == 0)
		{
			if (cmdbuff[8] < '0' || cmdbuff[8] > '7')
			{
				err("zly numer portu");
				continue;
			}
			int port = cmdbuff[8] - '0';
			
			char istr[2];
			istr[1] = '\0';
			
			int on = (~PINB & (1 << port));
			
			uart_write("PORT ");
			istr[0] = port + '0';
			uart_write(istr);
			if (on)
				uart_write(" IS 1, MASTER\n");
			else
				uart_write(" IS 0, MASTER\n");
		}
		else
			err("Zle polecenie");
	}
	
	return 0;
}
