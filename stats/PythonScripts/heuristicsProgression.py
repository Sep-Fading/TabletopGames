"""
This script is used to generate line graphs 
that show the progression of heuristics for each player
on average through out a game.

The data is taken from the csv files available in the stats folder.

Note that the csv files are not clean data, column separation is
altered by ActionStrings that contain commas (notably the Move action).

This script will sanitise the data and fix the columns before plotting.
"""

import pandas as pd
import matplotlib.pyplot as plt
import re

# Path to CSV file
csv_path = "huamn_round_2.csv"

# Custom parser to handle ActionString field data sanitisation
def parse_custom_csv_file(filepath):
    with open(filepath, 'r') as f:
        lines = f.readlines()

    headers = lines[0].strip().split(',')
    rows = []
    for line in lines[1:]:
        parts = re.split(r',(?=(?:[^\[]*\[[^\]]*\])*[^\]]*$)', line.strip())
        if len(parts) == len(headers):
            rows.append(parts)

    return pd.DataFrame(rows, columns=headers)

# Load and clean data
df = parse_custom_csv_file(csv_path)

# Convert column types
df["Game"] = df["Game"].astype(int)
df["PlayerID"] = df["PlayerID"].astype(int)
df["HeuristicValue"] = df["HeuristicValue"].astype(float)
df["RoundNum"] = df["RoundNum"].astype(int)

# Group by PlayerID and RoundNum across all games, compute average heuristic
agg_df = df.groupby(["PlayerID", "RoundNum"])["HeuristicValue"].mean().reset_index()

# Plot
plt.figure(figsize=(10, 6))
for player_id, group in agg_df.groupby("PlayerID"):
    plt.plot(group["RoundNum"], group["HeuristicValue"], label=f"Player {player_id}")

plt.xlabel("Round Number")
plt.ylabel("Average Heuristic Value")
plt.title("Average Heuristic Value per Round per Player (Across Games)")
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.show()
