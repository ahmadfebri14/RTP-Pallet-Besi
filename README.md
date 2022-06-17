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
