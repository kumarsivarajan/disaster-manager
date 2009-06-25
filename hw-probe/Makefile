CFLAGS = -mmcu=atmega16 -O1 --std=c99

all: hw-probe.hex

hw-probe: LCD4x40.c uart.c hw-probe.c
	avr-gcc $(CFLAGS) $? -o $@

hw-probe.hex: hw-probe
	avr-objcopy -O ihex $? $@

flash: hw-probe.hex
	sudo avrdude -y -c usbasp -p m16 -V -U flash:w:$?

clean:
	rm -f hw-probe
	rm -f hw-probe.hex
