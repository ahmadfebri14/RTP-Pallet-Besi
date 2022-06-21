# RTP-Pallet-Besi
Simple Barcode Scan for Zebra PDA TC26.

Before using this application, you need configure your PDA setting.
1. Launch DataWedge via Applications --> DataWedge

2. Select a Profile (Profile0 is used by DataWedge for all apps not explicitly assigned a Profile)

3. Confirm the following Profile settings:

    A. The Profile is enabled

    B. Barcode input is enabled

    C. Intent output is enabled

4. Configure the intent output as follows:

    A. Intent action: com.dwbasicintent1.ACTION (to match value defined in the strings.xml file)

    B. Intent category: (leave blank)

    C. Intent delivery: Broadcast intent
    
 
# How to use this app
There 3 barcode you need to scan:
1. Pallet

    You need to scan barcode that contain PBA or PPA on barcode

    eg: PPA82AA123, PPA1234567, PBA82AA987

2. Core

    You need to scan barcode that contain CBA on barcode

    eg: CBA82AA123, CBA1234567, CBA82AA987

3. Roll

    You need to scan barcode that contains number and has a length of 9 to 11 digits

    eg: 157551431, 920480997, 150206733
