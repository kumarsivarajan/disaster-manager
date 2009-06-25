/*******************************************************************************
 *
 * Biblioteka obsługi UART dla mikroprocesorów ATMega
 * Autor: Tomasz Wasilczyk (www.wasilczyk.pl)
 *
 *******************************************************************************
 *
 * linux - mozna uzyc programu minicom  dla ft232rl - urządzenie /dev/ttyUSB0
 *
 * Konfiguracja:
 * - 9600 Bps
 * - Parzystość: nie (8N1)
 * - Kontrola przepływu: nie
 *
 ******************************************************************************/

#include "config.h"
#include <avr/io.h>
#include <avr/interrupt.h>
#include <inttypes.h>
#include <avr/iom16.h>
#include <util/delay.h>
#include <string.h>

#define UART_BAUD_CALC(UART_BAUD_RATE,F_OSC) ((F_OSC)/((UART_BAUD_RATE)*16l)-1)

char uart_buff[UART_BUFF_LENGTH][UART_BUFF_LINELENGTH];
int uart_lineLength[UART_BUFF_LENGTH];
volatile int uart_currBuff = 0; // nr aktualnego bufora
volatile int uart_buffLastRead = 0; // ostatni przeczytany (uart_buffLastRead < uart_currBuff mod UART_BUFF_LENGTH)
volatile int uart_linePos = 0; // w aktualnym buforze

void uart_put(unsigned char c)
{
	while(!(UCSRA & (1 << UDRE))); // oczekiwanie na UDR
	UDR = c; // wyślij znak
}

void uart_write(char *s)
{
	while (*s)
		uart_put(*(s++));
}

// nie zapomnieć o sei()
void uart_init(void)
{
	// ustawienie szybkości
	UBRRH = (uint8_t)(UART_BAUD_CALC(UART_BAUD_RATE,F_OSC) >> 8);
	UBRRL = (uint8_t)UART_BAUD_CALC(UART_BAUD_RATE,F_OSC);

	// włączenie odbiornika i nadajnika, oraz przerwania
	UCSRB = (1 << RXEN) | (1 << TXEN) | (1 << RXCIE);

	// asynchroniczny z bitami stopu: 8-N-1
	UCSRC = (1 << URSEL) | (3 << UCSZ0);
	
	// włączenie przerwań
	sei();
}

// odbiór danych SERIAL
SIGNAL(SIG_UART_RECV)
{
	char c = UDR;
	if (c == '\r')
		return;
	if (c == '\n')
	{
		// nowa linia
		uart_lineLength[uart_currBuff] = uart_linePos;
		uart_linePos = 0;
		
		// czyli nowy bufor
		if (++uart_currBuff >= UART_BUFF_LENGTH)
			uart_currBuff = 0;
		
		// przepełnienie bufora (za dużo wiadomości)
		if (uart_currBuff == uart_buffLastRead)
			if (++uart_buffLastRead >= UART_BUFF_LENGTH)
				uart_buffLastRead = 0;
		
		return;
	}
	
	// przepełnienie bufora (za długa linia)
	if (uart_linePos + 1 >= UART_BUFF_LINELENGTH)
		return;
	
	uart_buff[uart_currBuff][uart_linePos++] = c;
}

int uart_read(char *buff, int bufflen)
{
	if (uart_currBuff == uart_buffLastRead)
		return 0;
	
	// nie wyłączamy przerwań -- mamy bufor 10 poleceń, może nic się nie stanie ;)
//	cli(); // wyłączenie przerwań
	
	int buffNo = uart_buffLastRead++;
	if (uart_buffLastRead == UART_BUFF_LENGTH)
		uart_buffLastRead = 0;
	int copied = uart_lineLength[buffNo];
	if (copied > bufflen)
		copied = bufflen;
	memcpy(buff, uart_buff[buffNo], copied);
	
//	sei(); // ponowne włączenie przerwań
	
	return copied;
}
