"""
Face Recognition API - Final Working Version
Đã fix tất cả bugs, chạy được ngay trên Google Colab
"""

import os, subprocess, json

# Install libraries
print("🔧 Installing...")
subprocess.run(["pip", "install", "-q", "insightface", "onnxruntime-gpu", "fastapi", "uvicorn", "python-multipart", "pyngrok", "nest-asyncio", "numpy", "opencv-python-headless", "scikit-learn"])
print("✅ Done!")

import cv2, numpy as np, insightface, uvicorn, nest_asyncio
from insightface.app import FaceAnalysis
from fastapi import FastAPI, File, UploadFile, HTTPException, Form
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from typing import List
from pyngrok import ngrok
from sklearn.metrics.pairwise import cosine_similarity
from datetime import datetime

# Load model
print("🤖 Loading InsightFace...")
app_face = FaceAnalysis(name='buffalo_l', providers=['CPUExecutionProvider'])
app_face.prepare(ctx_id=0, det_size=(640, 640))
print("✅ Model ready!")

# FastAPI app
app = FastAPI(title="Face Recognition API", version="1.0")
app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_credentials=True, allow_methods=["*"], allow_headers=["*"])

def decode_image(file_bytes):
    nparr = np.frombuffer(file_bytes, np.uint8)
    return cv2.imdecode(nparr, cv2.IMREAD_COLOR)

def calculate_similarity(v1, v2):
    return float(cosine_similarity(np.array(v1).reshape(1,-1), np.array(v2).reshape(1,-1))[0][0])

def euclidean_distance(v1, v2):
    return float(np.linalg.norm(np.array(v1) - np.array(v2)))

@app.get("/")
def home():
    return {"status": "running", "model": "InsightFace buffalo_l", "timestamp": datetime.now().isoformat()}

@app.get("/health")
def health():
    return {"status": "healthy", "gpu": "CUDAExecutionProvider" in str(app_face.rec_model.session.get_providers())}

@app.post("/extract-vector")
async def extract_vector(file: UploadFile = File(...)):
    try:
        contents = await file.read()
        img = decode_image(contents)
        if img is None:
            raise HTTPException(400, "Invalid image")
        
        faces = app_face.get(img)
        if not faces:
            return {"success": False, "message": "No face detected", "face_count": 0}
        
        faces = sorted(faces, key=lambda x:(x.bbox[2]-x.bbox[0])*(x.bbox[3]-x.bbox[1]), reverse=True)
        face = faces[0]
        
        return {
            "success": True,
            "vector": face.embedding.tolist(),
            "vector_dimension": len(face.embedding),
            "face_count": len(faces),
            "gender": "Male" if face.sex==1 else "Female",
            "age": int(face.age),
            "quality_score": float(face.det_score)
        }
    except Exception as e:
        return JSONResponse(500, {"success": False, "error": str(e)})

@app.post("/verify")
async def verify(file: UploadFile = File(...), target_vector: str = Form(...)):
    try:
        # Clean vector string
        import re
        cleaned = target_vector.strip()
        cleaned = re.sub(r'\s+', ' ', cleaned)
        cleaned = cleaned.replace('[ ','[').replace(' ]',']').replace(' ,',',').replace(', ',',')
        
        try:
            target_emb = json.loads(cleaned)
            if not isinstance(target_emb, list):
                raise ValueError("Must be array")
        except:
            raise HTTPException(400, "Invalid vector format")
        
        contents = await file.read()
        img = decode_image(contents)
        if img is None:
            raise HTTPException(400, "Invalid image")
        
        faces = app_face.get(img)
        if not faces:
            return {"success": False, "is_match": False, "message": "No face", "confidence": 0.0}
        
        faces = sorted(faces, key=lambda x:(x.bbox[2]-x.bbox[0])*(x.bbox[3]-x.bbox[1]), reverse=True)
        current_emb = faces[0].embedding.tolist()
        
        similarity = calculate_similarity(current_emb, target_emb)
        distance = euclidean_distance(current_emb, target_emb)
        
        THRESHOLD = 0.35
        is_match = similarity >= THRESHOLD
        
        return {
            "success": True,
            "is_match": is_match,
            "confidence": float(similarity),
            "similarity": float(similarity),
            "distance": float(distance),
            "thresholds": {"similarity_threshold": THRESHOLD},
            "face_quality": float(faces[0].det_score),
            "message": "Match!" if is_match else "No match"
        }
    except HTTPException:
        raise
    except Exception as e:
        return JSONResponse(500, {"success": False, "error": str(e)})

@app.post("/extract-vectors-batch")
async def batch_extract(files: List[UploadFile] = File(...)):
    """Train with multiple images - returns average vector"""
    try:
        if len(files) < 2:
            return JSONResponse(400, {"success": False, "message": "Need at least 2 images"})
        if len(files) > 10:
            return JSONResponse(400, {"success": False, "message": "Max 10 images"})
        
        vectors = []
        for file in files:
            try:
                contents = await file.read()
                img = decode_image(contents)
                if img is None:
                    continue
                faces = app_face.get(img)
                if not faces:
                    continue
                faces = sorted(faces, key=lambda x:(x.bbox[2]-x.bbox[0])*(x.bbox[3]-x.bbox[1]), reverse=True)
                vectors.append(faces[0].embedding.tolist())
            except:
                continue
        
        if not vectors:
            return {"success": False, "message": "No faces detected in any image"}
        
        avg_vector = np.mean(vectors, axis=0).tolist()
        
        return {
            "success": True,
            "average_vector": avg_vector,
            "vector_dimension": len(avg_vector),
            "images_processed": len(vectors),
            "message": f"Averaged {len(vectors)} vectors"
        }
    except Exception as e:
        return JSONResponse(500, {"success": False, "error": str(e)})

# Ngrok setup
NGROK_TOKEN = "361KmKS0DMFLJVszSYfDFMOR8b8_4E59TivRBuuMq444Ac8T2"
if NGROK_TOKEN:
    ngrok.set_auth_token(NGROK_TOKEN)

# Start server
port = 8000
try:
    public_url = ngrok.connect(port).public_url
    print("\n" + "="*60)
    print("🚀 FACE RECOGNITION API RUNNING!")
    print("="*60)
    print(f"📍 URL: {public_url}")
    print(f"\n📋 Endpoints:")
    print(f"   • {public_url}/extract-vector")
    print(f"   • {public_url}/verify")
    print(f"   • {public_url}/extract-vectors-batch  (NEW - Train nhiều ảnh)")
    print(f"   • {public_url}/docs")
    print(f"\n💡 Add to application.properties:")
    print(f"   face.recognition.api.url={public_url}")
    print("="*60 + "\n")
except Exception as e:
    print(f"⚠️ Ngrok error: {e}")

nest_asyncio.apply()
config = uvicorn.Config(app, host="0.0.0.0", port=port, log_level="info")
server = uvicorn.Server(config)
await server.serve()
