"""
This script is used to generate a bar chart
that shows the number of times each action was used
per player on average throughout 100 games.

The data is taken from the csv files available in the stats folder.

Note that the csv files are not clean data, column separation is
altered by ActionStrings that contain commas (notably the Move action).

This script will sanitise the data and fix the columns before plotting.
"""

import pandas as pd
import matplotlib.pyplot as plt
import re

# Path to CSV file
csv_path = "human_round_1.csv"

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

# Load the CSV
df = parse_custom_csv_file(csv_path)

# Convert types
df["Game"] = df["Game"].astype(int)
df["PlayerID"] = df["PlayerID"].astype(int)

# Count actions per Game, PlayerID, and ActionType
action_counts = df.groupby(["Game", "PlayerID", "ActionType"]).size().reset_index(name="ActionCount")

# Now average action counts across all games, per Player and ActionType
avg_counts = action_counts.groupby(["PlayerID", "ActionType"])["ActionCount"].mean().reset_index()

# Create labels for bar chart
avg_counts["Label"] = avg_counts["PlayerID"].astype(str) + " - " + avg_counts["ActionType"].str.replace("TOVPlayer", "")

# Plot bar chart
plt.figure(figsize=(12, 6))
plt.bar(avg_counts["Label"], avg_counts["ActionCount"])

plt.xlabel("Player - ActionType")
plt.ylabel("Average Count Per Game")
plt.title("Average Action Type Usage per Player per Game")
plt.xticks(rotation=45, ha='right')
plt.tight_layout()
plt.grid(axis='y')
plt.show()